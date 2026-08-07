#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>
#include <time.h>

// Generates a random 64-bit signed integer
int64_t rand64(void) {
    uint64_t result = 0;
    for (int i = 0; i < 5; i++) {
        result = (result << 15) ^ (rand() & 0x7FFF);
    }
    return (int64_t)result;
}

int main(void) {
    srand((unsigned int)time(NULL));

    int64_t num1 = rand64();
    int64_t num2 = rand64();

    // Portable 64-bit format specification for "%d, %d" output style
    printf("%" PRId64 "L, %" PRId64 "L\n", num1, num2);

    return 0;
}