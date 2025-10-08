package net.devs.electromod.block.custom.magnetic.MagneticForce;

import java.util.Set;

public record ForceProfile(Set<EVec3i> Core1, Set<EVec3i>[] Core1Profile1, Set<EVec3i>[] Core1Profile2,
                           Set<EVec3i> Core2, Set<EVec3i>[] Core2Profile1, Set<EVec3i>[] Core2Profile2, Set<EVec3i>[] Core2Profile3,
                           Set<EVec3i> Core3, Set<EVec3i>[] Core3Profile1)
{



//    private static Set<EVec3i> createCore(int core, Direction forceDirection)
//    {
//
//    }
//
//    // core1 profiles
//    private static Set<EVec3i>[] createCore1Profile1(Direction forceDirection)
//    {
//
//    }
//
//    private static Set<EVec3i>[] createCore1Profile2(Direction forceDirection)
//    {
//
//    }
//
//    // core2 profiles
//    private static Set<EVec3i>[] createCore2Profile1(Direction forceDirection)
//    {
//
//    }
//
//    private static Set<EVec3i>[] createCore2Profile2(Direction forceDirection)
//    {
//
//    }
//
//    private static Set<EVec3i>[] createCore2Profile3(Direction forceDirection)
//    {
//
//    }
//
//    // core3 profile
//    private static Set<EVec3i>[] createCore3Profile1(Direction forceDirection)
//    {
//
//    }
}
