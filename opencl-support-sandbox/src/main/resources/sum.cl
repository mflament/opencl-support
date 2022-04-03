kernel void mySum(global const T* a,
                  global const T* b,
                  global T* results,
                  int length)
{
    int global_id = get_global_id(0);
    if (global_id < length)
        results[global_id] = a[global_id] + b[global_id];
}
