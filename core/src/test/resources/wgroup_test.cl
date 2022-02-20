kernel void wgtest(global const uint dummy[8]) {
    printf("wgtest: (%d, %d, %d)\n", (int)get_global_id(0), (int)get_group_id(0), (int)get_local_id(0));
}
