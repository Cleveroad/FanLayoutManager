package com.cleveroad.testrecycler;

public class SportCardModel {
    public String sportTitle;
    private String sportSubtitle;
    private String sportRound;

    private int imageResId;

    private String time;
    private String dayPart;

    private int backgroundColorResId;

    private SportCardModel(Builder builder) {
        sportTitle = builder.sportTitle;
        sportSubtitle = builder.sportSubtitle;
        sportRound = builder.sportRound;
        imageResId = builder.imageResId;
        time = builder.time;
        dayPart = builder.dayPart;
        backgroundColorResId = builder.backgroundColorResId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSportTitle() {
        return sportTitle;
    }

    public String getSportSubtitle() {
        return sportSubtitle;
    }

    public String getSportRound() {
        return sportRound;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTime() {
        return time;
    }

    public String getDayPart() {
        return dayPart;
    }

    public int getBackgroundColorResId() {
        return backgroundColorResId;
    }

    /**
     * {@code SportCardModel} builder static inner class.
     */
    public static final class Builder {
        private String sportTitle;
        private String sportSubtitle;
        private String sportRound;
        private int imageResId;
        private String time;
        private String dayPart;
        private int backgroundColorResId;

        private Builder() {
        }

        /**
         * Sets the {@code sportTitle} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param sportTitle the {@code sportTitle} to set
         * @return a reference to this Builder
         */
        public Builder withSportTitle(String sportTitle) {
            this.sportTitle = sportTitle;
            return this;
        }

        /**
         * Sets the {@code sportSubtitle} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param sportSubtitle the {@code sportSubtitle} to set
         * @return a reference to this Builder
         */
        public Builder withSportSubtitle(String sportSubtitle) {
            this.sportSubtitle = sportSubtitle;
            return this;
        }

        /**
         * Sets the {@code sportRound} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param sportRound the {@code sportRound} to set
         * @return a reference to this Builder
         */
        public Builder withSportRound(String sportRound) {
            this.sportRound = sportRound;
            return this;
        }

        /**
         * Sets the {@code imageResId} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param imageResId the {@code imageResId} to set
         * @return a reference to this Builder
         */
        public Builder withImageResId(int imageResId) {
            this.imageResId = imageResId;
            return this;
        }

        /**
         * Sets the {@code time} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param time the {@code time} to set
         * @return a reference to this Builder
         */
        public Builder withTime(String time) {
            this.time = time;
            return this;
        }

        /**
         * Sets the {@code dayPart} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param dayPart the {@code dayPart} to set
         * @return a reference to this Builder
         */
        public Builder withDayPart(String dayPart) {
            this.dayPart = dayPart;
            return this;
        }

        /**
         * Sets the {@code backgroundColorResId} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param backgroundColorResId the {@code backgroundColorResId} to set
         * @return a reference to this Builder
         */
        public Builder withBackgroundColorResId(int backgroundColorResId) {
            this.backgroundColorResId = backgroundColorResId;
            return this;
        }

        /**
         * Returns a {@code SportCardModel} built from the parameters previously set.
         *
         * @return a {@code SportCardModel} built with parameters of this {@code SportCardModel.Builder}
         */
        public SportCardModel build() {
            return new SportCardModel(this);
        }
    }
}
