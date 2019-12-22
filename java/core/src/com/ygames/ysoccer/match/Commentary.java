package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;

import java.util.*;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final Commentary instance = new Commentary();

    public static final Commentary getInstance() { return instance; }

    /**
     * A comment element
     */
    public static class Comment {
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

    public enum Priority {
        LOW(1), COMMON(2), HIGH(3), GOAL(4);

        private int weight;
        Priority(int weight) { this.weight = weight; }
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
    private Sound playing = null;

    private long since = 0L;
    private float lastLength = 0F;

    private Timer timer = new Timer();

    /**
     * Enqueue a comment
     * @param elements
     */
    public void enqueueComment(Comment[] elements) {

        // A comment with greater priority comes
        if (!queue.isEmpty() && queue.peek()[0].priority.weight > elements[0].priority.weight) {
            queue.clear();
        }

        queue.add(elements);
    }

    private boolean pullAndPlay() {

        if (current.isEmpty()) {
            return false;
        }

        Comment target = current.poll();

        OpenALSound openALSound = (OpenALSound) target.getSound();

        lastLength = openALSound.duration();
        System.out.println(System.currentTimeMillis() + " duracion: " + lastLength);
        since = System.currentTimeMillis();

        playing = openALSound;
        playing.play();
        System.out.println(System.currentTimeMillis() + " reproduce");

        return true;
    }

    public void wake() {
        timer.schedule(new TimerTask() {
            public void run()  {
                tick();
            }
        }, 1, 10);
    }

    public void tick() {

        long now = System.currentTimeMillis();

        if (playing != null && now > since + lastLength * 1000) {
            System.out.println(System.currentTimeMillis() + " Se termina");
            playing = null;
        }

        if (playing == null) {
            if (pullAndPlay()) {
                return;
            }
        }

        if (!current.isEmpty() && playing == null) {
            Comment newCurrent = current.poll();

            playing = newCurrent.sound;

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

        playing.stop();

        playing = null;
        current.clear();
        queue .clear();
    }

}
