kernel void mySqrt(global double* a,
                   global double* results,
                   int length) {
    int global_id = get_global_id(0);
    if (global_id < length)
        results[global_id] = sqrt(a[global_id]);
}