typedef struct TestStruct {
    uint a;
    uint b[20];
    float8 c[4];
} TestStruct;

kernel void firstKernel(global const int* gInts,
                    local int* lInts,
                    int pInt,
                    read_only image2d_t roImage,
                    write_only image2d_t rwImage) {
}

kernel __attribute__((work_group_size_hint(1, 1, 1))) void secondKernel(
                          constant const uint16* arg0,
                          global int8* arg1,
                          local float3* arg2) {
}

kernel void thirdKernel(global const TestStruct* ts) {
}
