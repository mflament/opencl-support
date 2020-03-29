
kernel void wgtest() {
  int gindex = get_global_id(0) * get_global_size(1) + get_global_id(1);
  int lindex = get_local_id(0) * get_local_size(1) + get_local_id(1);
  int gid0 = get_group_id(0);
  int gid1 = get_group_id(1);
  printf("groupid: (%d,%d); gindex: %d; lindex: %d\n",gid0,gid1,  gindex, lindex);

  /*
  for (int i = 1; i < index; i++) {
    barrier(CLK_GLOBAL_MEM_FENCE);
  }
  printf("%d %d\n", index, groupId);
  */
  //printf("%2v4hld\n", ids);
  //barrier(CLK_GLOBAL_MEM_FENCE);
}
