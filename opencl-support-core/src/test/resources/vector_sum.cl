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
