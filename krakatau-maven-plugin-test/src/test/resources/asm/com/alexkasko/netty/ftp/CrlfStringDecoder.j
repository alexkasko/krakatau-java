.version 50 0
.source CrlfStringDecoder.java
.class public super com/alexkasko/netty/ftp/CrlfStringDecoder
.super org/jboss/netty/handler/codec/frame/FrameDecoder

.field private static final CR B = 13
.field private static final LF B = 10
.field private final maxRequestLengthBytes I
.field private final encoding Ljava/nio/charset/Charset;

.method public <init> : ()V
	.limit stack 3
	.limit locals 1
	aload_0
	sipush 256
	ldc 'UTF-8'
	invokespecial com/alexkasko/netty/ftp/CrlfStringDecoder <init> (ILjava/lang/String;)V
	return
.end method

.method public <init> : (ILjava/lang/String;)V
	.limit stack 5
	.limit locals 3
	aload_0
	invokespecial org/jboss/netty/handler/codec/frame/FrameDecoder <init> ()V
	iload_1
	ifgt L37
	new java/lang/IllegalArgumentException
	dup
	new java/lang/StringBuilder
	dup
	ldc 'Provided maxRequestLengthBytes: ['
	invokespecial java/lang/StringBuilder <init> (Ljava/lang/String;)V
	iload_1
	invokevirtual java/lang/StringBuilder append (I)Ljava/lang/StringBuilder;
	ldc '] must be positive'
	invokevirtual java/lang/StringBuilder append (Ljava/lang/String;)Ljava/lang/StringBuilder;
	invokevirtual java/lang/StringBuilder toString ()Ljava/lang/String;
	invokespecial java/lang/IllegalArgumentException <init> (Ljava/lang/String;)V
	athrow
L37:
.stack full
	locals Object com/alexkasko/netty/ftp/CrlfStringDecoder Integer Object java/lang/String
	stack 
.end stack
	aload_0
	iload_1
	putfield com/alexkasko/netty/ftp/CrlfStringDecoder maxRequestLengthBytes I
	aload_0
	aload_2
	invokestatic java/nio/charset/Charset forName (Ljava/lang/String;)Ljava/nio/charset/Charset;
	putfield com/alexkasko/netty/ftp/CrlfStringDecoder encoding Ljava/nio/charset/Charset;
	return
.end method

.method protected decode : [_67]
.throws java/lang/Exception
	.limit stack 5
	.limit locals 7
	aload_0
	getfield com/alexkasko/netty/ftp/CrlfStringDecoder maxRequestLengthBytes I
	newarray byte
	astore 4
	iconst_0
	istore 5
L11:
.stack append
	locals Object [B Integer
.end stack
	aload_3
	invokeinterface org/jboss/netty/buffer/ChannelBuffer readable ()Z 1
	ifne L28
	aload_3
	invokeinterface org/jboss/netty/buffer/ChannelBuffer resetReaderIndex ()V 1
	aconst_null
	areturn
L28:
.stack same
	aload_3
	invokeinterface org/jboss/netty/buffer/ChannelBuffer readByte ()B 1
	istore 6
	iload 6
	bipush 13
	if_icmpne L72
	aload_3
	invokeinterface org/jboss/netty/buffer/ChannelBuffer readByte ()B 1
	istore 6
	iload 6
	bipush 10
	if_icmpne L11
	new java/lang/String
	dup
	aload 4
	aload_0
	getfield com/alexkasko/netty/ftp/CrlfStringDecoder encoding Ljava/nio/charset/Charset;
	invokespecial java/lang/String <init> ([BLjava/nio/charset/Charset;)V
	areturn
L72:
.stack append
	locals Integer
.end stack
	iload 6
	bipush 10
	if_icmpne L93
	new java/lang/String
	dup
	aload 4
	aload_0
	getfield com/alexkasko/netty/ftp/CrlfStringDecoder encoding Ljava/nio/charset/Charset;
	invokespecial java/lang/String <init> ([BLjava/nio/charset/Charset;)V
	areturn
L93:
.stack same
	iload 5
	aload_0
	getfield com/alexkasko/netty/ftp/CrlfStringDecoder maxRequestLengthBytes I
	if_icmplt L134
	new java/lang/IllegalArgumentException
	dup
	new java/lang/StringBuilder
	dup
	ldc 'Request size threshold exceeded: ['
	invokespecial java/lang/StringBuilder <init> (Ljava/lang/String;)V
	aload_0
	getfield com/alexkasko/netty/ftp/CrlfStringDecoder maxRequestLengthBytes I
	invokevirtual java/lang/StringBuilder append (I)Ljava/lang/StringBuilder;
	ldc ']'
	invokevirtual java/lang/StringBuilder append (Ljava/lang/String;)Ljava/lang/StringBuilder;
	invokevirtual java/lang/StringBuilder toString ()Ljava/lang/String;
	invokespecial java/lang/IllegalArgumentException <init> (Ljava/lang/String;)V
	athrow
L134:
.stack same
	aload 4
	iload 5
	iload 6
	bastore
	iinc 5 1
.stack chop 1
	goto L11
.end method

.const [_67] = Utf8 (Lorg/jboss/netty/channel/ChannelHandlerContext;Lorg/jboss/netty/channel/Channel;Lorg/jboss/netty/buffer/ChannelBuffer;)Ljava/lang/Object;