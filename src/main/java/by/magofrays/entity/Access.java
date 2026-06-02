package by.magofrays.entity;

public enum Access {
    // admin
    ADD_MEMBER,
    REMOVE_MEMBER,
    UPDATE_FAMILY,
    MANAGE_ROLE,
    SHOW_ROLES,
    MANAGE_MEMBER_ROLES,
    GENERATE_INVITE_LINK,
    // constraints
    SHOW_MEMBERS,
    SHOW_CHAT,
    // tasks
    UPDATE_TASK,
    DELETE_TASK,
    SHOW_TASKS,
    CREATE_TASK,
    ASSIGN_TASK,
    // messages
    DELETE_MESSAGE,
    REACT_MESSAGE,
    CREATE_MESSAGE,
    EDIT_MESSAGE
}
