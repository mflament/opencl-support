kernel void wgtest() {
    printf("wgtest: (%d, %d, %d)\n", (int)get_global_id(0), (int)get_group_id(0), (int)get_local_id(0));
}

kernel void testArgs(private int a) {
    //printf("a[0]: %d, a[1]: %d, a[2]: %d)\n", a.x, a.y, a.z);
    printf("a: %d\n", a);
}
