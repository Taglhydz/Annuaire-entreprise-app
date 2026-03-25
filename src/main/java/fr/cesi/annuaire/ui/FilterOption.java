package fr.cesi.annuaire.ui;

public class FilterOption {

    private final Long id;
    private final String label;

    public FilterOption(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return label;
    }
}
