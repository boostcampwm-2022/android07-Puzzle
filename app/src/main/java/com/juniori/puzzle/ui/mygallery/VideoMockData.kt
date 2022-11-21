package com.juniori.puzzle.ui.mygallery

data class VideoMockData(
    val location: String,
    val thumbnailUrl: String
){

    companion object {
        fun mockList(start: Int): List<VideoMockData> {
            val list = mutableListOf<VideoMockData>()
            for (i in 1 + start..40 + start) {
                list.add(VideoMockData("$i", ""))
            }
            return list
        }
    }
}
