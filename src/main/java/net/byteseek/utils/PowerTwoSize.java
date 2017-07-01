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
 * An enumeration of power of two sizes from 2 (power 2^1) to 1G (power 2^30).
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

    private int powerTwoSize;

    PowerTwoSize(final int powerTwoSize) {
        this.powerTwoSize = powerTwoSize;
    }

    public int getPowerTwo() {
        return powerTwoSize;
    }

    public int getSize() {
        return 1 << powerTwoSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getSize() + ")";
    }
}
