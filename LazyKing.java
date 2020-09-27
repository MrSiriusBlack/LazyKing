package su.itline;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class LazyKing {
    private static List<String> pollResults = new LinkedList<>();

    public static void main(String[] args) throws IOException {
	// write your code here
        // Заполняем список из файла, переданного в параметре
        fillPollResults(args[0]);
        UnluckyVassal unluckyVassal = new UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);
    }

    private static void fillPollResults(String filename) throws IOException{
        pollResults = Files.readAllLines(Paths.get(filename));
    }
}

class UnluckyVassal {
    public void printReportForKing(List<String> pollResults) throws IOException {
        Set<Person> patrials = fillPatrials(pollResults);
        Path path = Paths.get("output.txt");
        Files.writeString(path, "король\n", StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        writeReportToFile(patrials.stream().filter(p -> p.master == null).collect(Collectors.toCollection(TreeSet::new)), path, 1);
    }

    private Set<Person> fillPatrials(List<String> pollResults) {
        Set<Person> patrials = new TreeSet<>();

        for (String pollResult : pollResults) {
            String[] patrial = pollResult.split(":");
            Person masterPerson = checkPatrial(patrial[0].trim(), patrials);
            if (patrial.length > 1) { // есть рабы
                String[] slaves = slaves = patrial[1].split(",");
                for (String slave : slaves) {
                    Person slavePerson = checkPatrial(slave.trim(), patrials);
                    slavePerson.master = masterPerson;
                    masterPerson.slaves.add(slavePerson);
                    patrials.add(slavePerson);
                }
            }
            patrials.add(masterPerson);
        }
        return patrials;
    }

    private Person checkPatrial(final String patrialName, Set<Person> patrials) {
        return patrials.stream().filter(p -> p.equals(new Person(patrialName))).findFirst().orElse(new Person(patrialName));
    }

    private void writeReportToFile(Set<Person> patrials, Path path, int level) throws IOException{
        for (Person person : patrials) {
            Files.writeString(path, nameWithTabs(person.name, level), StandardOpenOption.APPEND);
            if (!person.slaves.isEmpty())
                writeReportToFile(person.slaves, path, level + 1);
        }
    }

    private String nameWithTabs(String name, int tabs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabs; ++i)
            sb.append('\t');
        return sb.append(name).append('\n').toString();
    }
}

class Person implements Comparable {
    public Person master;
    public String name;
    public Set<Person> slaves = new TreeSet<>();

    public Person(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareToIgnoreCase(((Person) o).name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
