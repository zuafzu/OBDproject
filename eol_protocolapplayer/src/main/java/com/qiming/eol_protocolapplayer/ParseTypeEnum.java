package com.qiming.eol_protocolapplayer;

public enum ParseTypeEnum {
	Null(0),
	EnumType(1),
	StringType(2),
	BCDType(3),
	SignedIntType(4),
	UnsignedIntType(5),
	SignedFloatType(6),
	UnsignedFloatType(7),
	SectionType(8),
	ModeType(10),
	TimeType(11),
	HEXType(12),
	
	;
	private int value;


	private ParseTypeEnum(int value) {
		this.value = value;
	}

	public static ParseTypeEnum valueOf(int value) 
	{
        switch (value) {
        case 0:
            return Null;
        case 1:
            return EnumType;
        case 2:
            return StringType;
        case 3:
            return BCDType;
        case 4:
            return SignedIntType;
        case 5:
            return UnsignedIntType;
        case 6:
            return SignedFloatType;
        case 7:
            return UnsignedFloatType;
        case 8:
            return SectionType;
        case 10:
            return ModeType;
        case 11:
            return TimeType;
        case 12:
            return HEXType;
        default:
            return null;
        }
    }


    public int value() {
        return this.value;
    }
}