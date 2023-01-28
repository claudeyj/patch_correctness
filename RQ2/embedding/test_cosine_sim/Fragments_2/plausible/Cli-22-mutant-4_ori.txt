    private void processNonOptionToken(String value)
    {
            eatTheRest = true;
            tokens.add("--");

        tokens.add(value);
    }
