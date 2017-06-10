package design.com.java.build;

public class BuilderBean {

    private boolean watchDogs2;

    private boolean civilization6;

    private boolean darkSoul3;

    private boolean bloodBorne;

    public BuilderBean(Builder builder){
        this.watchDogs2 = builder.watchDogs2;
        this.civilization6 = builder.civilization6;
        this.darkSoul3 = builder.darkSoul3;
        this.bloodBorne = builder.bloodBorne;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.watchDogs2){
            stringBuilder.append("看门狗2。");
        }
        if (this.civilization6){
            stringBuilder.append("文明6。");
        }
        if (this.darkSoul3){
            stringBuilder.append("黑暗之魂3。");
        }
        if (this.bloodBorne){
            stringBuilder.append("血源诅咒。");
        }
        return stringBuilder.toString();
    }

    public static final class Builder{

        private boolean watchDogs2;

        private boolean civilization6;

        private boolean darkSoul3;

        private boolean bloodBorne;

        public Builder(){}

        public Builder buyWatchDogsTwo(){
            this.watchDogs2 = true;
            return this;
        }

        public Builder buyCivilizationSix(){
            this.civilization6 = true;
            return this;
        }

        public Builder buyDarkSoulThree(){
            this.darkSoul3 = true;
            return this;
        }

        public Builder buyBloodBorne(){
            this.bloodBorne = true;
            return this;
        }

        public BuilderBean build(){
            return new BuilderBean(this);
        }
    }
}
