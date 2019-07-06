/*
 * Copyright Matt Palmer 2017, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.byteseek.utils;

/**
 * An enumeration of power of two sizes from 2 (power 2^1) to 1Gig (power 2^30).
 *
 * Created by matt on 01/07/17.
 */
public enum PowerTwoSize {

    SIZE_2(1),    SIZE_4(2),     SIZE_8(3),     SIZE_16(4),    SIZE_32(5),
    SIZE_64(6),   SIZE_128(7),   SIZE_256(8),   SIZE_512(9),   SIZE_1K(10),
    SIZE_2K(11),  SIZE_4K(12),   SIZE_8K(13),   SIZE_16K(14),  SIZE_32K(15),
    SIZE_64K(16), SIZE_128K(17), SIZE_256K(18), SIZE_512K(19), SIZE_1M(20),
    SIZE_2M(21),  SIZE_4M(22),   SIZE_8M(23),   SIZE_16M(24),  SIZE_32M(25),
    SIZE_64M(26), SIZE_128M(27), SIZE_256M(28), SIZE_512M(29), SIZE_1G(30);

    //TODO: what about power two of zero? i.e. 1?
    /**
     * A static utility method to return the enum value for a given power of two size.
     *
     * @param powerTwoSize The power of two size enum to return given a power of two, between 1 an 30 inclusive.
     * @return A PowerTwoSize enum for the power of two passed in.
     * @throws IllegalArgumentException if the powerTwoSize is not between 1 and 30 inclusive.
     */
    public static final PowerTwoSize valueOf(final int powerTwoSize) {
        switch(powerTwoSize) {
            case 1:  return SIZE_2;
            case 2:  return SIZE_4;
            case 3:  return SIZE_8;
            case 4:  return SIZE_16;
            case 5:  return SIZE_32;
            case 6:  return SIZE_64;
            case 7:  return SIZE_128;
            case 8:  return SIZE_256;
            case 9:  return SIZE_512;
            case 10: return SIZE_1K;
            case 11: return SIZE_2K;
            case 12: return SIZE_4K;
            case 13: return SIZE_8K;
            case 14: return SIZE_16K;
            case 15: return SIZE_32K;
            case 16: return SIZE_64K;
            case 17: return SIZE_128K;
            case 18: return SIZE_256K;
            case 19: return SIZE_512K;
            case 20: return SIZE_1M;
            case 21: return SIZE_2M;
            case 22: return SIZE_4M;
            case 23: return SIZE_8M;
            case 24: return SIZE_16M;
            case 25: return SIZE_32M;
            case 26: return SIZE_64M;
            case 27: return SIZE_128M;
            case 28: return SIZE_256M;
            case 29: return SIZE_512M;
            case 30: return SIZE_1G;
            default: throw new IllegalArgumentException("Power two size must be between 1 and 30 inclusive.");
        }
    }

    /**
     * The underlying power of two the enum represents.
     */
    private int powerTwoSize;

    /**
     * Constructor for the enum.
     *
     * @param powerTwoSize The power two size of the enum.
     */
    PowerTwoSize(final int powerTwoSize) {
        this.powerTwoSize = powerTwoSize;
    }

    /**
     * Returns the power of two the enum represents.
     *
     * @return the power of two the enum represents.
     */
    public int getPowerTwo() {
        return powerTwoSize;
    }

    /**
     * Returns the size represented by the enumeration.
     *
     * @return the size represented by the enumeration.
     */
    public int getSize() {
        return 1 << powerTwoSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getSize() + ")";
    }
}
