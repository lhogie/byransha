#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

static const char BASE62_ALPHABET[] =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

/**
 * Generates 16 random bytes formatted as a RFC 4122 Version 4 UUID.
 */
void generate_uuid_v4(uint8_t uuid[16]) {
    int fd = open("/dev/urandom", O_RDONLY);
    if (fd >= 0) {
        ssize_t bytes_read = read(fd, uuid, 16);
        (void)bytes_read;
        close(fd);
    } else {
        for (int i = 0; i < 16; i++) {
            uuid[i] = rand() & 0xFF;
        }
    }

    // Set RFC 4122 variant (10xxxxxx) and version 4 (0100xxxx)
    uuid[6] = (uuid[6] & 0x0F) | 0x40;
    uuid[8] = (uuid[8] & 0x3F) | 0x80;
}

/**
 * Converts a 128-bit UUID byte array into a fixed 22-character Base62 string.
 */
void uuid_to_base62(const uint8_t uuid[16], char out[23]) {
    // Pack 16 bytes into a single 128-bit integer
    unsigned __int128 val = 0;
    for (int i = 0; i < 16; i++) {
        val = (val << 8) | uuid[i];
    }

    // Extract 22 Base62 digits from right to left (auto-padded with '0')
    out[22] = '\0';
    for (int i = 21; i >= 0; i--) {
        out[i] = BASE62_ALPHABET[val % 62];
        val /= 62;
    }
}

int main(void) {
    uint8_t uuid[16];
    char b62[23];

        generate_uuid_v4(uuid);
        uuid_to_base62(uuid, b62);
        printf("%s\n", b62);

    return 0;
}