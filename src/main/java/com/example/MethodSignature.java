package com.example;

import java.util.List;

public record MethodSignature(String className, String returnType, List<String> argTypes) {}
