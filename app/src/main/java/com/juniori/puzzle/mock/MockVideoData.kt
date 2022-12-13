package com.juniori.puzzle.mock

import com.juniori.puzzle.domain.entity.VideoInfoEntity

private val imageList = mutableListOf<String>()

fun getVideoListMockData(): List<VideoInfoEntity> {
    imageList.add("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.bas.ac.uk%2Fabout%2Fantarctica%2Fwildlife%2Fpenguins%2Fgentoo-penguin%2F&psig=AOvVaw0Y3OInta9o0_aG9uZa_c3Q&ust=1668739380989000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCMjuieKYtPsCFQAAAAAdAAAAABAE")
    imageList.add("https://www.google.com/url?sa=i&url=https%3A%2F%2Febird.org%2Fspecies%2Femppen1&psig=AOvVaw1l91xJkBqgSou5xP6BIm2c&ust=1668743625836000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCPjQmcqotPsCFQAAAAAdAAAAABAE")
    imageList.add("https://a-z-animals.com/media/2021/11/King-Penguin.jpg")
    imageList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS24Ljtjfgtso5HSKYtXL0GrjkqX8V9-5ENpw&usqp=CAU")
    imageList.add("https://www.google.com/imgres?imgurl=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fd%2Fd1%2FAnas_platyrhynchos_LC0014.jpg&imgrefurl=https%3A%2F%2Fko.wikipedia.org%2Fwiki%2F%25EC%25B2%25AD%25EB%2591%25A5%25EC%2598%25A4%25EB%25A6%25AC&tbnid=R8WX__ScJDABlM&vet=12ahUKEwiv1LuQrLT7AhWkS_UHHYOhBREQMygAegUIARDRAQ..i&docid=SolPAt4BnclHQM&w=1976&h=1413&q=%EC%B2%AD%EB%91%A5%EC%98%A4%EB%A6%AC&ved=2ahUKEwiv1LuQrLT7AhWkS_UHHYOhBREQMygAegUIARDRAQ")
    imageList.add("https://mblogthumb-phinf.pstatic.net/MjAxODAxMDJfMTA3/MDAxNTE0ODgwNTY2OTc0.8LxADt55g2SY3nM6FZFh4AH2Xm5vn7AHsfwH5EfJ_lsg.NDA_6myIp2mrcRtOd35i1U--oaVVek2nbhY7nODTL5og.PNG.zoopark01/image_1492814851514880561326.png?type=w800")
    imageList.add("https://post-phinf.pstatic.net/MjAxOTA3MjRfMjcw/MDAxNTYzOTI3ODc2NTUy.DfC_wCMby4GxvJss2Q2gFxeZd6KMAziG_xG0a5VyNSEg.P5v_AulrhVk1OR-ai69TMvVaijsUyNeh6dsIIGQngS0g.JPEG/photo-1544460848-32344b7004ec.jpg?type=w1200")

    return listOf(
        VideoInfoEntity("a","aaa", "aaa_300", imageList[0], false, 0, emptyList(), 105, "서대문구A", emptyList(), "젠투펭귄"),
        VideoInfoEntity("b","bbb", "bbb_400", imageList[1], true, 0, emptyList(), 101, "서대문구B", emptyList(), "황제펭귄"),
        VideoInfoEntity("a","aaa", "aaa_500", imageList[2], false, 0, emptyList(), 103, "마포구", emptyList(), "킹펭귄"),
        VideoInfoEntity("d","ddd", "ddd_100", imageList[3], true, 0, emptyList(), 102, "은평구", emptyList(), "턱끈펭귄"),
        VideoInfoEntity("e","eee", "eee_700", imageList[4], false, 0, emptyList(), 100, "동대문구", emptyList(), "청둥오리"),
        VideoInfoEntity("f","fff", "fff_600", imageList[5], true, 0, emptyList(), 104, "동작구", emptyList(), "노랑오리"),
        VideoInfoEntity("a","aaa", "aaa_800", imageList[1], true, 0, emptyList(), 106, "서대문구A", emptyList(), "도시오리"),
        VideoInfoEntity("a","aaa", "aaa_900", imageList[2], false, 0, emptyList(), 106, "서대문구B", emptyList(), "어라?"),
        VideoInfoEntity("a","aaa", "aaa_950", imageList[3], true, 0, emptyList(), 106, "종로구", emptyList(), "어?"),
        VideoInfoEntity("a","aaa", "aaa_800", imageList[1], true, 0, emptyList(), 106, "서대문구A", emptyList(), "도시오리"),
        VideoInfoEntity("a","aaa", "aaa_900", imageList[2], false, 0, emptyList(), 106, "서대문구B", emptyList(), "어라?"),
        VideoInfoEntity("a","aaa", "aaa_950", imageList[3], true, 0, emptyList(), 106, "종로구", emptyList(), "어?")
   )
}