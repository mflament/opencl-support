kernel void sum(global const int* a,
                global const int* b,
                global int* results,
                int length)
{
    int global_id = get_global_id(0);
    if (global_id < length)
        results[global_id] = a[global_id] + b[global_id];
}