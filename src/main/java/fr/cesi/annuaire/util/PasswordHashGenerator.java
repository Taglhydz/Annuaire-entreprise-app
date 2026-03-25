package fr.cesi.annuaire.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHashGenerator {

    private PasswordHashGenerator() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: PasswordHashGenerator <plain-password>");
            return;
        }

        String hash = BCrypt.hashpw(args[0], BCrypt.gensalt(12));
        System.out.println(hash);
    }
}
