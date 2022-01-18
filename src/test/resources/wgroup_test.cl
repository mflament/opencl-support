
kernel void wgtest() {
  int3 gid = (int3) (get_global_id(0), get_global_id(1), get_global_id(2));
  int3 lid = (int3) (get_local_id(0), get_local_id(1), get_local_id(2));
  int3 grp = (int3) (get_group_id(0), get_group_id(1), get_group_id(2));

//  printf("global: (%d, %d, %d);\n", gid.x, gid.y, gid.z);
  printf("global: (%d, %d, %d); local:  (%d, %d, %d); group:  (%d, %d, %d)\n",
        gid.x, gid.y, gid.z,
        lid.x, lid.y, lid.z,
        grp.x, grp.y, grp.z);
}
