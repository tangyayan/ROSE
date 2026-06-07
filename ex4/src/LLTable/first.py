
non_terminals = set()
productions_raw = []

with open("grammar.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()

        if "::=" not in line:
            continue

        lhs, rhs = line.split("::=", 1)

        lhs = lhs.strip()
        rhs = rhs.strip()

        non_terminals.add(lhs)
        productions_raw.append((lhs, rhs))

terminals = set()
first_sets = {nt: set() for nt in non_terminals}
follow_sets = {nt: set() for nt in non_terminals}

follow_sets["START"].add("EOF")

for lhs, rhs in productions_raw:
    # 按 | 分开产生式
    alternatives = rhs.split("|")
    for alt in alternatives:
        symbols = alt.strip().split()
        for sym in symbols:
            if sym in non_terminals:
                continue
            if sym in {"/*", "empty", "*/"}:
                continue
            terminals.add(sym)
            first_sets[sym] = {sym}

# first
while(True):
    updated = False
    for lhs, rhs in productions_raw:
        alternatives = rhs.split("|")
        for alt in alternatives:
            symbols = alt.strip().split()
            if symbols == ['/*', 'empty', '*/']:
                if "empty" not in first_sets[lhs]:
                    updated = True
                    first_sets[lhs].add("empty")
                continue
            for sym in symbols:
                target = first_sets[sym] - {"empty"}
                if not target.issubset(first_sets[lhs]):
                    updated = True
                    first_sets[lhs].update(target)
                
                if "empty" not in first_sets[sym]:
                    break
                else:
                    if "empty" not in first_sets[lhs]:
                        updated = True
                        first_sets[lhs].add("empty")

    if not updated:
        break


# follow
while(True):
    updated = False
    for lhs, rhs in productions_raw:
        alternatives = rhs.split("|")
        for alt in alternatives:
            symbols = alt.strip().split()
            if symbols == ['/*', 'empty', '*/']:
                continue

            right_first = first_sets[symbols[-1]] - {"empty"}
            for i in range(len(symbols)-2, -1, -1):
                if symbols[i] in non_terminals: # 非终结符
                    if not right_first.issubset(follow_sets[symbols[i]]):
                        updated = True
                        follow_sets[symbols[i]].update(right_first)

                    if "empty" in first_sets[symbols[i]]:
                        right_first.update(first_sets[symbols[i]] - {"empty"})
                    else:
                        right_first = first_sets[symbols[i]] - {"empty"}
                else: # 终结符
                    right_first = first_sets[symbols[i]] - {"empty"}
            
            # 最后一个符号的 follow 集合要加上 lhs 的 follow 集合
            for i in range(len(symbols)-1, -1, -1):
                if symbols[i] in non_terminals:
                    if not follow_sets[lhs].issubset(follow_sets[symbols[i]]):
                        updated = True
                        follow_sets[symbols[i]].update(follow_sets[lhs])
                    if "empty" not in first_sets[symbols[i]]:
                        break
                else:
                    break

    if not updated:
        break

with open("first_follow.txt", "w", encoding="utf-8") as f:
    f.write("First sets:\n")
    for nt in sorted(non_terminals):
        f.write(f"FIRST({nt}) = {{ {', '.join(sorted(first_sets[nt]))} }}\n")

    f.write("\nFollow sets:\n")
    for nt in sorted(non_terminals):
        f.write(f"FOLLOW({nt}) = {{ {', '.join(sorted(follow_sets[nt]))} }}\n")