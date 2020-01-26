package com.ygames.ysoccer.framework;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;

import java.util.*;

import static com.ygames.ysoccer.framework.GLGame.LogType.AI_ATTACKING;
import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";
    private static final float MAX_QUEUE = 3.0f;
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
        playing.getSound().play();
        lastSound = playing.getSound();

        return true;
    }

    /**
     * Awakens the commentary thread
     */
    public void wake() {

        GLGame.debug(COMMENTARY, this, "Waking commentary subsystem");

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
