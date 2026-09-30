package com.api.tca.common.helpers;
import java.nio.charset.StandardCharsets;

public final class AudioSignature {

    public static final int HEADER_SIZE = 16;

    public static boolean isSupported(byte[] header) {
        return isWav(header)
                || isMp3(header)
                || isAac(header)
                || isOgg(header)
                || isFlac(header)
                || isM4a(header)
                || isWebm(header);
    }

    /** RIFF no offset 0 e WAVE no offset 8 (só RIFF também seria AVI/WebP) */
    public static boolean isWav(byte[] h) {
        return startsWith(h, 0, "RIFF") && startsWith(h, 8, "WAVE");
    }

    /** Tag ID3 ou frame com 11 bits de sync e camada válida */
    public static boolean isMp3(byte[] h) {
        return startsWith(h, 0, "ID3")
                || (h.length >= 2
                && (h[0] & 0xFF) == 0xFF
                && (h[1] & 0xE0) == 0xE0
                && (h[1] & 0x06) != 0x00);
    }

    /** ADTS: FF F1 ou FF F9 */
    public static boolean isAac(byte[] h) {
        return h.length >= 2 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xF6) == 0xF0;
    }

    /** Inclui Opus em contêiner Ogg */
    public static boolean isOgg(byte[] h) {
        return startsWith(h, 0, "OggS");
    }

    public static boolean isFlac(byte[] h) {
        return startsWith(h, 0, "fLaC");
    }

    /** "ftyp" no offset 4 (contêiner MP4/M4A; não distingue áudio de vídeo) */
    public static boolean isM4a(byte[] h) {
        return startsWith(h, 4, "ftyp");
    }

    public static boolean isWebm(byte[] h) {
        return startsWith(h, 0, new byte[]{0x1A, 0x45, (byte) 0xDF, (byte) 0xA3});
    }

    private static boolean startsWith(byte[] h, int offset, String ascii) {
        return startsWith(h, offset, ascii.getBytes(StandardCharsets.US_ASCII));
    }

    private static boolean startsWith(byte[] h, int offset, byte[] signature) {
        if (h == null || h.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (h[offset + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
