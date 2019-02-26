package ru.hse.kuzyaka.phonebook;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity for storing phone numbers used in the {@link PhoneBook}.
 */
@Entity
public class PhoneNumber {
    @Id
    private ObjectId id;
    @Reference
    private List<Person> persons = new ArrayList<>();
    @Indexed(unique = true)
    private String number;

    /**
     * Constructs phone number with the specific number
     *
     * @param number number used to construct phone number
     */
    public PhoneNumber(@NotNull String number) {
        this.number = number;
    }

    /** Empty constructor, used for mapping purposes */
    public PhoneNumber() {

    }

    /**
     * Returns all persons with the stored number
     *
     * @return all persons with the stored number
     */
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * Returns the stored number
     *
     * @return the stored number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Attaches the stored number to the specific person (no operations with database are used)
     *
     * @param person person to attach number to
     */
    public void addPerson(@NotNull Person person) {
        persons.add(person);
    }

    /**
     * Detaches the stored number from the specific person (no operations with database are used)
     *
     * @param person person to detach number from
     */
    public void removePerson(@NotNull Person person) {
        persons.remove(person);
    }

    /**
     * Checks if any person stored in the database has the stored number
     *
     * @return {@code true} if some person has this number; {@code false} otherwise
     */
    public boolean hasAny() {
        return !persons.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneNumber that = (PhoneNumber) o;
        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}