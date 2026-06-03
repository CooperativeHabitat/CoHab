package by.magofrays.entity;

import lombok.Getter;

@Getter
public enum Access {
    // admin
    ADD_MEMBER("Доступ добавлять новых участников"),
    REMOVE_MEMBER("Доступ исключать участников из семьи"),
    RENAME_FAMILY("Доступ переименовывать семью"),
    MANAGE_ROLE("Доступ создавать и изменять роли"),
    SHOW_ROLES("Доступ видеть роли семьи"),
    MANAGE_MEMBER_ROLES("Доступ назначать и отзывать роли от участников"),
    GENERATE_INVITE_LINK("Доступ создавать пригласительный код в семью"),
    // constraints
    SHOW_MEMBERS("Доступ видеть других членов семьи"),
    SHOW_CHAT("Доступ видеть чат семьи"),
    // tasks
    UPDATE_TASK("Доступ изменять задачи семьи"),
    DELETE_TASK("Доступ удалять задачи семьи"),
    SHOW_TASKS("Доступ видеть задачи семьи"),
    CREATE_TASK("Доступ создавать задачи"),
    ASSIGN_TASK("Доступ назначать задачи на других участников семьи"),
    // messages
    DELETE_MESSAGE("Доступ удалять чужие сообщения"),
    REACT_MESSAGE("Доступ реагировать на чужие сообщения"),
    CREATE_MESSAGE("Доступ отправлять сообщения в чат"),
    EDIT_MESSAGE("Доступ изменять сообщения в чате");

    private final String description;
    Access(String description) {
        this.description = description;
    }
}
