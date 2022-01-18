kernel void vector_sqrt(global const float* values,
                global float* results,
                int const size) {
  int gid = get_global_id(0);
  if (gid < size)
      results[gid] = sqrt(values[gid]);
}
