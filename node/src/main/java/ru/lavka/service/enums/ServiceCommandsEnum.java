package ru.lavka.service.enums;

public enum ServiceCommandsEnum {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),;

    private final String cmd;

    ServiceCommandsEnum(String cmd) {
        this.cmd = cmd;
    }


    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.cmd.equals(cmd);
    }
}
