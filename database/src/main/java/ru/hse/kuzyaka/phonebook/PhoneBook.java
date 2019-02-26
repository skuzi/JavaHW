package ru.hse.kuzyaka.phonebook;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for storing phone book. Database contains pairs phone-number where one number can belong to different persons
 * and one person can have several numbers.
 */
public class PhoneBook {
    final private Morphia morphia = new Morphia();
    final private Datastore datastore;

    public PhoneBook() {
        datastore = morphia.createDatastore(new MongoClient("localhost", 27017), "phonebook");
        morphia.mapPackage("ru.hse.kuzyaka.phonebook");
        datastore.ensureIndexes();
    }

    /**
     * Returns {@link List} of phone numbers of the specific person
     *
     * @param name name used to select numbers
     * @return {@link List} of phone numbers of the specific person
     */
    public List<PhoneNumber> getNumbersByPerson(@NotNull String name) {
        @Nullable Person person = getPerson(name);
        return person != null ? person.getNumbers() : null;
    }

    /**
     * Returns {@link List} of persons with the specific number
     *
     * @param number number used to select persons
     * @return {@link List} of persons with the specific number
     */
    public List<Person> getPersonsByNumber(@NotNull String number) {
        @Nullable PhoneNumber phoneNumber = getNumber(number);
        return phoneNumber != null ? phoneNumber.getPersons() : null;
    }

    /**
     * Adds specific record to this phonebook. Returns {@code true} if the record was added and {@code false}
     * if the phonebook already contains such record
     *
     * @param name   name of the person
     * @param number phone number of the person
     * @return {@code true} if the record was added; {@code false} otherwise
     */
    public boolean addRecord(@NotNull String name, @NotNull String number) {
        Person person = getPersonOrCreate(name);
        PhoneNumber phoneNumber = getNumberOrCreate(number);

        if (person.getNumbers().contains(phoneNumber) || phoneNumber.getPersons().contains(person)) {
            return false;
        } else {
            phoneNumber.addPerson(person);
            datastore.save(phoneNumber);

            person.addNumber(phoneNumber);
            datastore.save(person);
            return true;
        }
    }

    /**
     * Deletes specific record from this phonebook. Returns {@code true} if the record was deleted and {@code false}
     * if the phonebook didn't contain such record
     *
     * @param name   name of the person
     * @param number phone number of the person
     * @return {@code true} if the record was deleted; {@code false} otherwise
     */
    public boolean deleteRecord(@NotNull String name, @NotNull String number) {
        Person person = getPerson(name);
        PhoneNumber phoneNumber = getNumber(number);
        if (person == null || phoneNumber == null) {
            return false;
        } else {
            person.removeNumber(phoneNumber);
            datastore.save(person);

            phoneNumber.removePerson(person);
            datastore.save(phoneNumber);

            if (!phoneNumber.hasAny()) {
                datastore.delete(phoneNumber);
            }
            if (!person.hasAny()) {
                datastore.delete(person);
            }
            return true;
        }
    }

    /**
     * Changes name in the specific pair name-number. Returns {@code true} if name was changed and {@code false}
     * if the phonebook didn't contain such pair
     *
     * @param number  number of the person
     * @param oldName name to change
     * @param newName new name
     * @return
     */
    public boolean changeName(@NotNull String number, @NotNull String oldName, @NotNull String newName) {
        if (!deleteRecord(oldName, number)) {
            return false;
        } else {
            return addRecord(newName, number);
        }
    }

    /**
     * Changes number in the specific pair name-number. Returns {@code true} if number was changed and {@code false}
     * if the phonebook didn't contain such pair
     *
     * @param name      name of the person
     * @param oldNumber number to change
     * @param newNumber new number
     * @return {@code true} if number was changed; {@code false} otherwise
     */
    public boolean changeNumber(@NotNull String name, @NotNull String oldNumber, @NotNull String newNumber) {
        if (!deleteRecord(name, oldNumber)) {
            return false;
        } else {
            return addRecord(name, newNumber);
        }
    }

    /**
     * Returns all pairs stored in the phonebook in form of {@link List} of {@link Record}.
     *
     * @return all pairs stored in the phonebook
     */
    public List<Record> getAllRecords() {
        List<Person> persons = datastore.createQuery(Person.class).asList();
        return persons.stream().
                flatMap(person -> person.getNumbers().stream().
                        map(number -> new Record(person, number))).
                collect(Collectors.toList());
    }

    /**
     * Clears phonebook.
     */
    public void clear() {
        datastore.getCollection(Person.class).drop();
        datastore.getCollection(PhoneNumber.class).drop();
    }

    private Person getPerson(@NotNull String name) {
        return datastore.createQuery(Person.class).field("name").equal(name).get();
    }

    private PhoneNumber getNumber(@NotNull String number) {
        return datastore.createQuery(PhoneNumber.class).field("number").equal(number).get();
    }

    private Person getPersonOrCreate(@NotNull String name) {
        Person person = getPerson(name);
        if (person == null) {
            person = new Person(name);
            datastore.save(person);
        }
        return person;
    }

    private PhoneNumber getNumberOrCreate(@NotNull String number) {
        PhoneNumber phoneNumber = getNumber(number);
        if (phoneNumber == null) {
            phoneNumber = new PhoneNumber(number);
            datastore.save(phoneNumber);
        }
        return phoneNumber;
    }

    /** Class for storing data which is {@link Person} and {@link PhoneNumber} */
    public static class Record {
        @NotNull
        private Person person;
        @NotNull
        private PhoneNumber number;

        /**
         * Constructs record with specified {@link Person} and {@link PhoneNumber}
         *
         * @param person {@link Person} used to construct record
         * @param number {@link PhoneNumber} used to construct recodd
         */
        public Record(@NotNull Person person, @NotNull PhoneNumber number) {
            this.person = person;
            this.number = number;
        }

        /**
         * Returns the name of the stored {@link Person}
         *
         * @return the name of the stored {@link Person}
         */
        public String getName() {
            return person.getName();
        }

        /**
         * Returns the phone number of the stored {@link PhoneNumber}
         *
         * @return the phone number of the {@link PhoneNumber}
         */
        public String getNumber() {
            return number.getNumber();
        }

    }
}