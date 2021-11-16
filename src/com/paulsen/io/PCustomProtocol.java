package com.paulsen.io;

public class PCustomProtocol {

	private String IDENTIFIER = "CP";

	private char BLOCK_START = '[', BLOCK_END = ']';
	/**
	 * Replacement if start- or end-character is included in data (1)=> store data
	 * with replaced characters (2)=> read data with replacements back replaced to
	 * start or end
	 */
	private String START_REPLACEMENT = "^<<^", END_REPLACEMENT = "^>>^";

	public PCustomProtocol() {
	}

	public PCustomProtocol(String identifier, char blockStart, char blockEnd, String startReplacement,
						   String endRemplacement) {
		IDENTIFIER = identifier;
		BLOCK_START = blockStart;
		BLOCK_END = blockEnd;
		START_REPLACEMENT = startReplacement;
		END_REPLACEMENT = endRemplacement;
	}

	public boolean isPartOfProtocol(String s) {
		s = trimEnd(trimStart(s));
		// Message must start immediatley with IDENTIFIER and BLOCK_START
		if (s.startsWith(IDENTIFIER + BLOCK_START) && s.endsWith(String.valueOf(BLOCK_END))) {
			String message = removeStart(s, IDENTIFIER.length());
			// Message can only contain BLOCK_START && BLOCK_END once
			if (count(message, BLOCK_START) != 1 || count(message, BLOCK_END) != 1) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * @param Protocol-Output
	 * @return Message
	 */
	public String getMessage(String input) {
		if (isPartOfProtocol(input)) {
			String message = removeStart(trimEnd(trimStart(input)), IDENTIFIER.length());

			// remove block-character and fill replcements
			String mN = "";
			for (int i = 1; i < message.length() - 1; i++)
				mN += message.charAt(i);
			message = mN.replace(START_REPLACEMENT, String.valueOf(BLOCK_START)).replace(END_REPLACEMENT,
					String.valueOf(BLOCK_END));

			return message;
		}
		return null;
	}

	/**
	 * @param Message that gets convertet into the protocol-format
	 * @return Protocol-Output
	 */
	public String getProtocolOutput(String message) {
		return IDENTIFIER + BLOCK_START + message.replace(String.valueOf(BLOCK_START), START_REPLACEMENT)
				.replace(String.valueOf(BLOCK_END), END_REPLACEMENT) + BLOCK_END;
	}

	private static String trimStart(String sIn) {
		String s = "";
		boolean hasBeenStart = false;
		for (int i = 0; i < sIn.length(); i++) {
			if (sIn.charAt(i) != ' ')
				hasBeenStart = true;
			if (hasBeenStart)
				s += sIn.charAt(i);
		}
		return s;
	}

	private static String removeStart(String in, int length) {
		String s = "";
		for (int i = length; i < in.length(); i++)
			s += in.charAt(i);
		return s;
	}

	private static String trimEnd(String sIn) {
		String s = "";
		boolean hasBeenEnd = false;
		for (int i = sIn.length() - 1; i >= 0; i--) {
			if (sIn.charAt(i) != ' ')
				hasBeenEnd = true;
			if (hasBeenEnd)
				s = sIn.charAt(i) + s;
		}
		return s;
	}

	private static int count(String s, char c) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c)
				count++;
		}
		return count;
	}
}
