package entity;

public class ActorWithCharacter {
    private Person actor;
    private String characterName;
    
    public ActorWithCharacter(Person actor, String characterName) {
        this.actor = actor;
        this.characterName = characterName;
    }
    
    public Person getActor() {
        return actor;
    }
    
    public void setActor(Person actor) {
        this.actor = actor;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}