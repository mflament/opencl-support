typedef TYPE T;

kernel void sum(global const T* values, 
                global T* results, 
                int const size,
                local T* partialSums) {
  int gid = get_global_id(0);
  int lid = get_local_id(0);
  int valueIndex = gid << 1;
  if (valueIndex < size) {
    partialSums[lid] = values[valueIndex] + values[valueIndex + 1];
  } else {
    partialSums[lid] = 0;
  }
  barrier(CLK_LOCAL_MEM_FENCE);

  int ls = get_local_size(0);
  for(unsigned int s = 1; s < ls; s *= 2) {
     int localIndex = (s << 1) * lid;     
     //printf("(%d,%d,%d,%d)\n",gid,lid,s,localIndex);
     if (localIndex < ls) {
       partialSums[localIndex] += partialSums[localIndex + s];
     }
     barrier(CLK_LOCAL_MEM_FENCE);
  }

  if (lid == 0) {
    results[get_group_id(0)] = partialSums[0];
  }
}
