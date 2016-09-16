package com.cleveroad.testrecycler;

import com.cleveroad.testrecycler.models.SportCardModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SportCardsUtils {

    public static Collection<SportCardModel> generateSportCards() {
        List<SportCardModel> sportCardModels = new ArrayList<>(5);

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Table tennis0")
                    .withSportSubtitle("Woman's singles")
                    .withSportRound("Preliminaries")
                    .withImageResId(R.drawable.pic_card_1)
                    .withTime("3:00")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.dark_orchid)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Shooting1")
                    .withSportSubtitle("Woman's 10m air rifle")
                    .withSportRound("Qualification")
                    .withImageResId(R.drawable.pic_card_2)
                    .withTime("2:30")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.mantis)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Rowing2")
                    .withSportSubtitle("Men's single sculls")
                    .withSportRound("Heats")
                    .withImageResId(R.drawable.pic_card_3)
                    .withTime("2:30")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.usc_gold)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Archery3")
                    .withSportSubtitle("Men's team")
                    .withSportRound("Round of 16")
                    .withImageResId(R.drawable.pic_card_5)
                    .withTime("3:00")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.portland_orange)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Water polo4")
                    .withSportSubtitle("Men’s tournament")
                    .withSportRound("Group A")
                    .withImageResId(R.drawable.pic_card_4)
                    .withTime("3:00")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.dodger_blue)
                    .build());

        }
        return sportCardModels;
    }
}
