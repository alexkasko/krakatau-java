; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)

.class public SkipJSR
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    aload_0
    dup
    invokestatic SkipJSR skipJSR ([Ljava/lang/String;)V
    invokestatic SkipJSR nestedJSRs ([Ljava/lang/String;)V
    return
.end method

.method public static nestedJSRs : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    ; Test a non-regular loop inside nested subprocedures

    aload_0
    arraylength
    istore_0

    sipush 1337
    istore_2

    iload_0
    jsr LSUB1
    iconst_0
    jsr LSUB1
    bipush 12
    jsr LSUB1
    return

LSUB1:
    swap
    istore_1
    jsr LSUB2
    astore_1
    ret 1

LSUB2:
    jsr LSUB3
    iconst_5
    istore_1
    jsr LSUB3
    astore_1
    ret 1

LSUB3:
    iload_1
    dup
    iconst_3
    irem

    ifeq LLOOP_ENTRY_2
LLOOP_ENTRY_1:
    iinc 2 -27
    jsr LPRINT

    dup
    iconst_2
    irem
    ifne LLOOP_TAIL

LLOOP_ENTRY_2:
    jsr LPRINT
    iinc 2 -7
    dup
    iflt LLOOP_EXIT

LLOOP_TAIL:
    iconst_m1
    iadd
    goto_w LLOOP_ENTRY_1
LLOOP_EXIT:
    swap
    astore_1
    pop
    ret 1

LPRINT:
    iinc 0 17
    getstatic java/lang/System out Ljava/io/PrintStream;
    iload 0
    iload_2
    imul
    invokevirtual java/io/PrintStream println (I)V
    astore_1
    ret 1

.end method

.method public static skipJSR : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    iconst_1
    istore_1
    jsr LSUB

    iconst_1
    newarray double
    dup
    astore_2
    iconst_0
    iload_1
    i2d
    dastore
    iinc 1 1

    jsr LSUB
    jsr LSUB
    aload_2
    iconst_0
    daload
    iload_1
    i2d
    ddiv
    dstore_0

    getstatic java/lang/System out Ljava/io/PrintStream;
    dload_0
    invokevirtual java/io/PrintStream println (D)V
    return

LS_2:
    arraylength
    iadd
    istore_1
    ret 3

LSUB:
    astore_3
    aload_0
    iload_1
    dup_x1
    lookupswitch
        default : LS_2
.end method
