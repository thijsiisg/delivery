#!/usr/bin/python
import sys
import yaml
from pprint import pprint

def main():
    # Header
    print("""
digraph G {
    K = 3.5

    node [
        fontname = "DejaVu Sans"
        shape = record
    ]

    edge [
        fontcolor = "red"
        fontname = "DejaVu Sans"
        style = bold
        labeldistance = 5
    ]

    // ---
    """)

    data = yaml.load(open(sys.argv[1]))
    all_links = []
    for table in data:
        name = table["table"]
        fields = table["fields"]

        print("%s [ label=\"{%s|\\" % (name, name));

        prefixes = []
        fieldnames = []
        for field in fields:
            if isinstance(field, dict):
                prefixes.append(list(field.keys())[0])
                fieldnames.append(list(field.values())[0])
            else:
                prefixes.append("")
                fieldnames.append(field)

        if "links" in table:
            for link in table["links"]:
                link["from"] = name
                all_links.append(link)

        print("{{%s}|{%s}}}\" ]" % ("|".join(prefixes), "|".join(fieldnames)))

    for link in all_links:
        if link["from"] == link["to"]:
            print("%s -> %s [ headlabel = \"%s\" taillabel = \"%s\" headport = n tailport = n labeldistance = 4 ]" % (link["from"], link["to"], link["head"], link["tail"]))
        else:
            print("%s -> %s [ headlabel = \"%s\" taillabel = \"%s\" ]" % (link["from"], link["to"], link["head"], link["tail"]))

    # Footer
    print("}")

if __name__ == '__main__':
    main()
