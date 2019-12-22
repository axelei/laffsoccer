package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final Commentary instance = new Commentary();

    public static final Commentary getInstance() { return instance; }

    /**
     * A comment element
     */
    public class Comment {
        private Priority priority;
        private Sound sound;

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

    public enum Priority {
        LOW(1), COMMON(2), HIGH(3), GOAL(4);

        private int weight;
        Priority(int weight) { this.weight = weight; }
    }

    private Commentary() {}

    /**
     * Queue of comments
     */
    private Queue<Comment[]> queue = new PriorityQueue<>();

    /**
     * Currently playing comments
     */
    private Queue<Comment> current = null;

    /**
     * Current playing sound
     */
    private Sound playing = null;

    /**
     * Enqueue a comment
     * @param elements
     */
    public void enqueueComment(Comment[] elements) {

        // A comment with greater priority comes
        if (queue.peek()[0].priority.weight > elements[0].priority.weight) {
            queue.clear();
        }

        queue.add(elements);
    }

    public void tick() {

        if (playing != null) {
            /*
            if (playing.isPlaying()) {
                return;
            } else {
                playing = null;
            }
             */
        }

        if (!current.isEmpty()) {
            Comment newCurrent = current.poll();

            return;
        }

        if (!queue.isEmpty()) {
            Comment[] next = queue.poll();

            current = new PriorityQueue<>();
            current.addAll(Arrays.asList(next));

           return;
        }

    }

    public void stop() {

        playing.sound.stop();

        playing = null;
        current.clear();
        queue .clear();
    }

}
