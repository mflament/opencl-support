void sum(int local_id, int offset, int* values, int size, local int* partialSums) {
    if (local_id < s)
        partialSums[local_id] = values[local_id] + values[local_id + s];
    else if (local_id < size)
        partialSums[local_id] = values[local_id];
    else
        partialSums[local_id] = 0;

    barrier(CLK_LOCAL_MEM_FENCE);
    for (; s > 0; s >>= 1) {
        if (local_id < s)
            partialSums[local_id] += partialSums[local_id + s];
        barrier(CLK_LOCAL_MEM_FENCE);
    }
}

kernel void sum_kernel(global int* values,
                       global int* results,
                       int size,
                       local int* partialSums) {
    int local_id = get_local_id(0);
    int group_id = get_group_id(0);
    int group_size = get_local_size(0);
    sum(local_id, values + group_size * group_id, size, partialSums);
    if (local_id == 0)
        results[group_id] = partialSums[0];
}

//typedef struct block_data
//{
//	const uint header[HEADER_INTS];  //    0      : 20 (80 bytes)
//	const uint midstate[H_INTS];     //    20(80) : 8  (32 bytes)
//	const int hMaskOffset;           //    28(112): 1  (4 bytes)
//	const uint hMask;                //    29(116): 1  (4 bytes)
//							         //             30 (120 bytes)
//} block_data;
//
//kernel void hash_nonce(constant block_data* data,
//                       global uint* groupResults,
//                       local uint* localResults,
//                       const uint baseNonce)
//{
//}
//

struct tnode {
    int count;
    tnode *left, *right; // same as struct tnode *left, *right;
};
typedef struct tnode *tnode_p, tnode;
typedef char char_t, *char_p, (*fp)(void);

kernel void test_typedef(tnode tn,
                         struct tnode stn,
                         tnode_p tnp,
                         char c1,
                         char_t ct,
                         char_p cp,
                         fp cfp)
{
}
