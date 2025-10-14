package site.siredvin.cloudsolutions.util

import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag

fun MutableSet<Int>.toListTag(): ListTag {
    val tag = ListTag()
    this.forEach { tag.add(IntTag.valueOf(it)) }
    return tag
}