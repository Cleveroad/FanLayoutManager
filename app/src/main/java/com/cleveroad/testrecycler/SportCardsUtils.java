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
                    .withSportTitle("Table tennis")
                    .withSportSubtitle("Woman's singles")
                    .withSportRound("Preliminaries")
                    .withImageResId(R.drawable.pic_card_1)
                    .withImage("http://lorempixel.com/output/food-q-c-640-480-8.jpg")
                    .withTime("3:00")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.dark_orchid)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Shooting")
                    .withSportSubtitle("Woman's 10m air rifle")
                    .withSportRound("Qualification")
                    .withImage("http://lorempixel.com/output/food-q-c-640-480-4.jpg")
                    .withImageResId(R.drawable.pic_card_2)
                    .withTime("2:30")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.mantis)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Rowing")
                    .withSportSubtitle("Men's single sculls")
                    .withSportRound("Heats")
                    .withImage("http://lorempixel.com/output/food-q-c-640-480-5.jpg")
                    .withImageResId(R.drawable.pic_card_3)
                    .withTime("2:30")
                    .withDayPart("PM")
                    .withBackgroundColorResId(R.color.usc_gold)
                    .build());

        }

        {
            sportCardModels.add(SportCardModel
                    .newBuilder()
                    .withSportTitle("Archery")
                    .withSportSubtitle("Men's team")
                    .withImage("http://lorempixel.com/output/food-q-c-640-480-7.jpg")
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
                    .withSportTitle("Water polo")
                    .withSportSubtitle("Men’s tournament")
                    .withImage("http://lorempixel.com/output/food-q-c-640-480-3.jpg")
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
