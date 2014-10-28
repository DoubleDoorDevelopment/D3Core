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

import java.util.HashSet;

/**
 * Permission system stuff
 *
 * @author Dries007
 */
public class Group
{
    private HashSet<Node> nodes = new HashSet<>();
    private String name;
    private String parent;

    public Group()
    {
    }

    public Group(String name)
    {
        this.name = name;
    }

    public Group(String name, String parent)
    {
        this.name = name;
        this.parent = parent;
    }

    public String getName()
    {
        return name;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    public boolean hasPermissionFor(Node requestNode)
    {
        if (parent != null && PermissionsDB.INSTANCE.getGroup(parent).hasPermissionFor(requestNode)) return true;
        for (Node hadNode : nodes) if (hadNode.matches(requestNode)) return true;
        return false;
    }

    @Override
    public int hashCode()
    {
        int result = nodes.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + parent.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return name.equals(group.name) && nodes.equals(group.nodes) && parent.equals(group.parent);

    }

    public void addNode(String nodeString)
    {
        nodes.add(new Node(nodeString));
    }

    public boolean removeNode(String nodeString)
    {
        return nodes.remove(new Node(nodeString));
    }

    public HashSet<String> getNodes()
    {
        HashSet<String> strings = new HashSet<>();
        for (Node node : nodes) strings.add(node.toString());
        return strings;
    }
}
