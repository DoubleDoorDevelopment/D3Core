/*
 * Copyright (c) 2014,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the {organization} nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package net.doubledoordev.d3core.permissions;

import com.google.gson.*;
import net.doubledoordev.d3core.util.CoreConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

import static net.doubledoordev.d3core.util.CoreConstants.JOINER_DOT;

/**
 * Permission system stuff
 *
 * @author Dries007
 */
public class Node
{
    final String[] parts;

    public Node(String parts)
    {
        this.parts = parts.toLowerCase().split("\\.");
    }

    public Node(String... parts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            parts[i] = parts[i].toLowerCase();
        }
        this.parts = parts;
    }

    public boolean matches(Node requestNode)
    {
        if (this.equals(requestNode)) return true;
        for (int i = 0; i < this.parts.length && i < requestNode.parts.length; i++)
        {
            if (this.parts[i].equals("*")) return true;
            if (!this.parts[i].equals(requestNode.parts[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(parts);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return Arrays.equals(parts, node.parts);
    }

    @Override
    public String toString()
    {
        return JOINER_DOT.join(parts);
    }

    public Node append(String... extras)
    {
        return new Node(ArrayUtils.addAll(parts, extras));
    }

    public static class JsonHelper implements JsonSerializer<Node>, JsonDeserializer<Node>
    {
        @Override
        public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return new Node((String) context.deserialize(json, String.class));
        }

        @Override
        public JsonElement serialize(Node src, Type typeOfSrc, JsonSerializationContext context)
        {
            return context.serialize(src.toString());
        }
    }
}
