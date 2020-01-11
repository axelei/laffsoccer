package com.ygames.ysoccer.framework;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;

import java.util.*;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";
    private static final float MAX_QUEUE = 3.0f;
    private static final float SHORT_QUEUE = 0.5f;

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

    private Timer timer = new Timer();

    /**
     * Enqueue a comment
     * @param elements
     */
    public void enqueueComment(Comment... elements) {

        if (queueLength > MAX_QUEUE && playing != null && playing.priority.weight > elements[0].priority.weight) {
            return;
        }

        // A comment with greater priority comes (or queue is very long)
        if (playing != null && playing.priority.weight < elements[0].priority.weight && queueLength < SHORT_QUEUE || queueLength > MAX_QUEUE) {
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
        queue.add(elements);
    }

    public static Comment[] getComment(Assets.CommonComment.CommonCommentType type, Comment.Priority priority) {
        Random dice = new Random();

        List<Comment> result = new ArrayList<>();
        result.add(new Comment(priority, Assets.CommonComment.pull(type)));
        if (dice.nextInt(6) > 2) {
            Sound secSound = Assets.CommonComment.pullSecond(type);
            if (secSound != null) {
                result.add(new Comment(priority, Assets.CommonComment.pullSecond(type)));
            }
        }

        return result.toArray(new Comment[result.size()]);
    }

    private boolean pullAndPlay() {

        if (current.isEmpty()) {
            return false;
        }

        Comment target = current.poll();

        OpenALSound openALSound = (OpenALSound) target.getSound();

        lastLength = openALSound.duration();
        queueLength -= lastLength;
        since = System.currentTimeMillis();

        playing = target;
        playing.getSound().play();
        lastSound = playing.getSound();

        return true;
    }

    public void wake() {
        timer.schedule(new TimerTask() {
            public void run()  {
                tick();
            }
        }, 1, 50);
    }

    public void tick() {

        Thread.currentThread().setName(THREAD_NAME);

        long now = System.currentTimeMillis();

        if (now - lastChitChat > 20000) {
            Random rnd = new Random();
            if (rnd.nextInt((int) EMath.max(1, (now - lastChitChat))) > 35000) {
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

    public void stop() {

        timer.cancel();

        current.clear();
        queue.clear();

        if (playing != null) {
            playing.getSound().stop();
            playing = null;
        }

    }

}
