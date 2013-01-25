#!/usr/bin/python
import sys
import yaml
from pprint import pprint

def main():
    # Header
    print("""
digraph G {
    K = 2
    compound = true

    node [
        fontname = "DejaVu Sans"
        shape = record
    ]

    edge [
        fontcolor = "red"
        fontname = "DejaVu Sans"
        style = bold
        labeldistance = 4
    ]

    // ---
    """)

    # Print all elements
    data = yaml.load(open(sys.argv[1]))
    for item in data:
        delegate(item)

    # Footer
    print("}")

def delegate(data):
    name = None
    if "class" in data:
        name = print_class(data)
    elif "package" in data:
        name = print_pkg(data)

    if name and "links" in data:
        print_links(data, name)

def print_pkg(data):
    print("""
        subgraph cluster_{name} {{
            label = "{name}"
    """ .format(
        name=data["package"],
    ))

    if "items" in data:
        for item in data["items"]:
            delegate(item)

    for key, val in data.items():
        if key not in ("items", "links", "package"):
            print("%s = %s" % (key, val))
    print("}")
    return "cluster_"+data["package"]

def print_class(data):
    label = "{" + data["class"] + "|"
    if "attributes" in data:
        for attr in data["attributes"]:
            label += attr
            label += "\\l"
    label += "|"
    if "methods" in data:
        for meth in data["methods"]:
            label += meth
            label += "\\l"
    label += "}"

    print("""
        {name} [
            label = "{label}"
    """ .format(
        name=data["class"],
        label=label,
    ))
    for key, val in data.items():
        if key not in ("class", "attributes", "methods", "links"):
            print("%s = %s" % (key, val))
    print("]")
    return data["class"]

def print_links(data, name):
    for link in data["links"]:
        if isinstance(link, str):
            print("%s -> %s" % (name, link))
        elif isinstance(link, dict):
            if "from" not in link:
                link["from"] = name
            print("%s -> %s [" % (link["from"], link["to"]))
            for key, val in link.items():
                if key not in ("from", "to"):
                    print("%s = %s" % (key, val))
            print("]")

if __name__ == '__main__':
    main()
