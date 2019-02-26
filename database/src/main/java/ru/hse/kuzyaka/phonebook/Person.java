package ru.hse.kuzyaka.phonebook;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {
    @Id
    private ObjectId id;
    @Reference
    private List<PhoneNumber> numbers = new ArrayList<>();
    @Indexed(unique = true)
    private String name;

    /**
     * Constructs person with the specific name
     *
     * @param name name used to construct person
     */
    public Person(@NotNull String name) {
        this.name = name;
    }

    /** Empty constructor, used for mapping purposes */
    public Person() {

    }

    /**
     * Returns all numbers of this person
     * @return all numbers of this person
     */
    public List<PhoneNumber> getNumbers() {
        return numbers;
    }

    /**
     * Adds the specific number to this person
     * @param number number to add
     */
    public void addNumber(@NotNull PhoneNumber number) {
        numbers.add(number);
    }

    /**
     * Removes the specific number from this person
     * @param number number to remove
     */
    public void removeNumber(PhoneNumber number) {
        numbers.remove(number);
    }

    /**
     * Checks if this person has any phone number
     * @return {@code true} if this person has at least one phone number; {@code false} otherwise
     */
    public boolean hasAny() {
        return !numbers.isEmpty();
    }

    /**
     * Returns the name of this person
     * @return the name of this person
     */
    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}