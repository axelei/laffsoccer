package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;

import java.util.*;

import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";
    private static final float MAX_QUEUE = 2.0f;
    private static final float SHORT_QUEUE = 0.15f;

    private static final Commentary instance = new Commentary();

    public static final Commentary getInstance() { return instance; }

    /**
     * A comment element
     */
    public static class Comment {

        public enum Priority {
            CHITCHAT(4), LOW(1), COMMON(2), HIGH(3), GOAL(5);

            private int weight;
            Priority(int weight) { this.weight = weight; }
        }

        private Priority priority;
        private Sound sound;

        public Comment(Priority priority, Sound sound) {
            this.priority = priority;
            this.sound = sound;
        }

        public Priority getPriority() {
            return priority;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Sound getSound() {
            return sound;
        }

        public void setSound(Sound sound) {
            this.sound = sound;
        }
    }

    /**
     * This is meant to be a singleton
     */
    private Commentary() {}

    /**
     * Queue of comments
     */
    private Queue<Comment[]> queue = new LinkedList<>();

    /**
     * Currently playing comments
     */
    private Queue<Comment> current = new LinkedList<>();

    /**
     * Current playing sound
     */
    private Comment playing = null;

    private long lastChitChat = System.currentTimeMillis();

    private long since = 0L;
    private float lastLength = 0F;
    private float queueLength = 0F;
    private Sound lastSound = null;

    private Timer timer;

    /**
     * Enqueue a comment
     * @param elements
     */
    public synchronized void enqueueComment(Comment... elements) {

        if (elements == null || elements.length == 0) {
            GLGame.debug(COMMENTARY, null, "Queued null comment");
            return;
        }

        if (queueLength > MAX_QUEUE && playing != null && playing.priority.weight > elements[0].priority.weight && elements[0].priority != Comment.Priority.CHITCHAT) {
            GLGame.debug(COMMENTARY, elements, "Commentary not queued: queue too long: " + queueLength);
            return;
        }

        // A comment with greater priority comes (or queue is very long)
        if ((playing != null && playing.priority.weight < elements[0].priority.weight && queueLength < SHORT_QUEUE || queueLength > MAX_QUEUE) && elements[0].priority != Comment.Priority.CHITCHAT)  {
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: is not chitchat? " + (elements[0].priority != Comment.Priority.CHITCHAT));
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: higher priority? " + (playing == null? "(not playing)" : playing.priority.weight < elements[0].priority.weight));
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: short queue?" + (queueLength < SHORT_QUEUE));
            queue.clear();
            current.clear();
            queueLength = 0;
            since = 0L;
            if (lastSound != null) {
                lastSound.stop();
            }
        }

        for (Comment element : elements) {
            queueLength += ((OpenALSound) element.getSound()).duration();
        }
        GLGame.debug(COMMENTARY, queueLength, "Queue length: " + queueLength);
        queue.add(elements);
    }

    /**
     * Prepares and enqueue end game comment
     */
    public void endGameComment(Match match) {
        enqueueComment(Commentary.getComment(Assets.CommonComment.CommonCommentType.MATCH_END, Commentary.Comment.Priority.HIGH));
        Comment[] resultComment = buildResult(match);
        if (resultComment != null) {
            enqueueComment(resultComment);
        }
    }

    /**
     * Prepares a random comment of type and priority specified
     * @param type
     * @param priority
     * @return the composed comment
     */
    public static Comment[] getComment(Assets.CommonComment.CommonCommentType type, Comment.Priority priority) {

        GLGame.debug(COMMENTARY, priority, "Generating new comment: " + type);

        Random dice = new Random();

        List<Comment> result = new ArrayList<>();
        result.add(new Comment(priority, Assets.CommonComment.pull(type)));
        if (dice.nextInt(6) > 2) {
            Sound secSound = Assets.CommonComment.pullSecond(type);
            if (secSound != null) {
                result.add(new Comment(priority == Comment.Priority.HIGH ? Comment.Priority.COMMON : priority, Assets.CommonComment.pullSecond(type)));
            }
        }

        return result.toArray(new Comment[result.size()]);
    }

    /**
     * Builds a comment saying the result
     * @param match
     * @return
     */
    public static Comment[] buildResult(Match match) {
        Sound[] numbers = Assets.CommonComment.numbers;

        MatchStats home = match.stats[Match.HOME];
        MatchStats away = match.stats[Match.AWAY];
        Map<String, Assets.TeamCommentary> teams = Assets.TeamCommentary.teams;

        Assets.TeamCommentary homeName = teams.get(FileUtils.getTeamFromFile(match.team[Match.HOME].path));
        Assets.TeamCommentary awayName = teams.get(FileUtils.getTeamFromFile(match.team[Match.AWAY].path));

        if (numbers[(home.goals)] == null
            || numbers[(away.goals)] == null
            || homeName.teamName == null || awayName.teamName == null) {
            return null;
        }
        return new Comment[] {
                new Comment(Comment.Priority.HIGH, homeName.teamName),
                new Comment(Comment.Priority.HIGH, numbers[(home.goals)]),
                new Comment(Comment.Priority.HIGH, awayName.teamName),
                new Comment(Comment.Priority.HIGH, numbers[(away.goals)])
            };
    }

    /**
     * Builds a comment for half time
     * @param match
     * @return
     */
    public static Comment[] halfTime(Match match) {
        Set<Sound> sounds = new HashSet<>();

        MatchStats home = match.stats[Match.HOME];
        MatchStats away = match.stats[Match.AWAY];

        if (home.goals + away.goals > 5) {
            sounds.add(Assets.Sounds.manyGoalsHalfTime);
        }
        if (home.foulsConceded + away.foulsConceded > 15) {
            sounds.add(Assets.Sounds.violentMatch);
        }
        if (home.goals + away.goals == 0) {
            sounds.add(Assets.Sounds.noGoalsHalfTime);
        }
        if (home.goals + 3 < away.goals) {
            sounds.add(Assets.Sounds.awayTeamTrashing);
        }
        if (home.goals > away.goals + 3) {
            sounds.add(Assets.Sounds.localTeamTrashing);
        }

        if (!sounds.isEmpty()) {
            return new Comment[] {new Comment(Comment.Priority.LOW, EMath.getRandomSetElement(sounds))};
        }
        return null;
    }

    /**
     * Pulls a comment from the queue and plays it
     * @return whether it did or not
     */
    private boolean pullAndPlay() {

        if (current.isEmpty()) {
            return false;
        }

        Comment target = current.poll();

        GLGame.debug(COMMENTARY, target, "Pulling new comment: " + target);

        OpenALSound openALSound = (OpenALSound) target.getSound();

        lastLength = openALSound.duration();
        queueLength -= lastLength;
        since = System.currentTimeMillis();

        playing = target;
        try {
            playing.getSound().play();
        } catch (UnsatisfiedLinkError ex) {
            GLGame.debug(COMMENTARY, this, "Couldn't play comment: " + ex.getMessage());
        }
        lastSound = playing.getSound();

        return true;
    }

    /**
     * Awakens the commentary thread
     */
    public void wake() {

        GLGame.debug(COMMENTARY, this, "Waking commentary subsystem");
        lastChitChat = System.currentTimeMillis();

        since = System.currentTimeMillis();

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run()  {
                tick();
            }
        }, 1, 50);
    }

    public synchronized void tick() {

        Thread.currentThread().setName(THREAD_NAME);

        long now = System.currentTimeMillis();

        if (now - lastChitChat > 20000) {
            Random rnd = new Random();
            if (rnd.nextInt((int) EMath.max(1, (now - lastChitChat))) > 36000) {
                enqueueComment(getComment(Assets.CommonComment.CommonCommentType.CHITCHAT, Comment.Priority.CHITCHAT));
                lastChitChat = now;
            }
        }

        if (playing != null && now > since + ((long) (lastLength * 1000))) {
            playing = null;
        }

        if (playing == null) {
            if (pullAndPlay()) {
                return;
            }
        }

        if (!current.isEmpty() && playing == null) {
            Comment newCurrent = current.poll();

            playing = newCurrent;

            return;
        }

        if (!queue.isEmpty() && current.isEmpty()) {
            Comment[] next = queue.poll();

            if (next != null) {
                current.addAll(Arrays.asList(next));
            }

           return;
        }

    }

    /**
     * Stops the commentary thread
     */
    public void stop() {

        GLGame.debug(COMMENTARY, this, "Stopping commentary subsystem");

        timer.cancel();

        current.clear();
        queue.clear();

        if (playing != null) {
            playing.getSound().stop();
            playing = null;
        }

    }

}
