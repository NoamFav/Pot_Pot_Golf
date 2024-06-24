package com.um_project_golf.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The class responsible for managing the audio.
 */
public class AudioManager {
    private static final Logger log = LogManager.getLogger(AudioManager.class);
    private long device;
    private long context;
    private int buffer;
    private int source;
    private final String path;

    /**
     * The constructor of the audio manager.
     *
     * @param path The path to the audio file.
     */
    public AudioManager(String path) {
        this.path = path;
        init();
    }

    /**
     * Initializes the audio manager.
     */
    public void init() {
        String defaultDeviceName = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(defaultDeviceName);

        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (int[]) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    /**
     * Plays the sound.
     * Also calculates the pitch of the sound.
     * The pitch is calculated by dividing the actual sample rate by the target sample rate.
     * The target sample rate is 44100.
     */
    public void playSound() {
        // Generate buffers and sources
        buffer = AL10.alGenBuffers();
        source = AL10.alGenSources();

        // Load your audio data into a ByteBuffer
        float actualSampleRate = loadAndBufferAudio(path);

        // Calculate the correct pitch
        // Default sample rate
        float targetSampleRate = 44100;
        float pitch = actualSampleRate / targetSampleRate;

        // Set up source input
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_PITCH, pitch);
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);

        // Play the source
        AL10.alSourcePlay(source);
    }

    public void stopSound() {
        AL10.alSourceStop(source);
    }

    private float loadAndBufferAudio(String filePath) {
        File file = new File(filePath);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            float sampleRate = format.getSampleRate();

            byte[] audioBytes = stream.readAllBytes();
            ByteBuffer audioBuffer = BufferUtils.createByteBuffer(audioBytes.length);
            audioBuffer.put(audioBytes);
            audioBuffer.flip();

            int openALFormat = getOpenALFormat(format.getChannels(), format.getSampleSizeInBits());
            AL10.alBufferData(buffer, openALFormat, audioBuffer, (int) sampleRate);

            return sampleRate;
        } catch (UnsupportedAudioFileException | IOException e) {
            log.error("Failed to load audio file: {}", filePath, e);
        }
        return 0;
    }

    private int getOpenALFormat(int channels, int bitDepth) {
        if (channels == 1) {
            return bitDepth == 16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_MONO8;
        } else if (channels == 2) {
            return bitDepth == 16 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_STEREO8;
        }
        throw new IllegalArgumentException("Unsupported channel count: " + channels);
    }

    public void cleanup() {
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}

