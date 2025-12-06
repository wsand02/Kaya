package com.wilsandbrink.kaya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// todo add custom exception for invalid powerscheme

public record PowerScheme(String guid, String name) {
    public PowerScheme(String guid, String name) {
        this.guid = guid;
        this.name = name;
        if (!this.validatePowerScheme())
            throw new InvalidPowerScheme("The power scheme you entered is invalid: " + name());
    }

    public boolean validatePowerScheme() {
        Pattern p = Pattern.compile(
                "Power Scheme GUID:\\s*([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})"
        );
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"powercfg", "/list"});
            try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
                String s;
                while ((s = stdIn.readLine()) != null) {
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        if (guid.equalsIgnoreCase(m.group(1))) {
                            exec.waitFor();
                            return true;
                        }
                    }
                }
            }
            exec.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
