package com.pot_pot_golf.Core;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.*;

/** The class responsible for managing the audio. */
public class AudioManager {
    private static final Logger log = LogManager.getLogger(AudioManager.class);
    private long device;
    private long context;
    private int buffer;
    private int source;
    private final InputStream audioStream;

    /**
     * Constructor for the audio manager.
     *
     * @param audioStream The InputStream of the audio file.
     */
    public AudioManager(InputStream audioStream) {
        this.audioStream = audioStream;
        init();
    }

    /** Initializes the audio manager. */
    public void init() {
        String defaultDeviceName = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
        assert defaultDeviceName != null;
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
     * Plays the sound. Calculates the pitch based on the actual sample rate and a target sample
     * rate of 44100.
     */
    public void playSound() {
        // Generate buffers and sources
        buffer = AL10.alGenBuffers();
        source = AL10.alGenSources();

        // Load audio data into the buffer
        float actualSampleRate = loadAndBufferAudio(audioStream);

        // Calculate the correct pitch
        float targetSampleRate = 44100; // Default sample rate
        float pitch = actualSampleRate / targetSampleRate;

        // Set up source input
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_PITCH, pitch);
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);

        // Play the source
        AL10.alSourcePlay(source);
    }

    /** Stops the sound. */
    public void stopSound() {
        AL10.alSourceStop(source);
    }

    /**
     * Loads and buffers the audio from an InputStream.
     *
     * @param inputStream The InputStream of the audio file.
     * @return The sample rate of the audio.
     */
    private float loadAndBufferAudio(InputStream inputStream) {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream)) {
            AudioFormat format = stream.getFormat();
            float sampleRate = format.getSampleRate();

            // Read audio data into a ByteBuffer
            byte[] audioBytes = stream.readAllBytes();
            ByteBuffer audioBuffer = BufferUtils.createByteBuffer(audioBytes.length);
            audioBuffer.put(audioBytes);
            audioBuffer.flip();

            // Determine the OpenAL format
            int openALFormat = getOpenALFormat(format.getChannels(), format.getSampleSizeInBits());
            AL10.alBufferData(buffer, openALFormat, audioBuffer, (int) sampleRate);

            return sampleRate;
        } catch (UnsupportedAudioFileException | IOException e) {
            log.error("Failed to load audio from InputStream", e);
        }
        return 0;
    }

    /**
     * Gets the OpenAL format.
     *
     * @param channels The number of channels.
     * @param bitDepth The bit depth.
     * @return The OpenAL format.
     */
    private int getOpenALFormat(int channels, int bitDepth) {
        if (channels == 1) {
            return bitDepth == 16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_MONO8;
        } else if (channels == 2) {
            return bitDepth == 16 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_STEREO8;
        }
        throw new IllegalArgumentException("Unsupported channel count: " + channels);
    }

    /** Cleans up the audio manager. */
    public void cleanup() {
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
