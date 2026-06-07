import random

MAX_DEPTH = 250
VAR_COUNT = 50
PROC_COUNT = 20
STMT_PER_BLOCK = 50

var_names = [f"v{i}" for i in range(VAR_COUNT)]


def gen_expr(depth=0):
    if depth > 10 or random.random() < 0.4:
        return random.choice(var_names + [str(random.randint(0, 1000))])

    op = random.choice(["+", "-", "*", "DIV"])
    return f"({gen_expr(depth+1)} {op} {gen_expr(depth+1)})"


def gen_assign():
    return f"{random.choice(var_names)} := {gen_expr()}"


def gen_if(depth):
    cond = f"{random.choice(var_names)} > {random.randint(0,100)}"

    then_part = gen_stmt(depth + 1)

    if random.random() < 0.5:
        else_part = gen_stmt(depth + 1)
        return (
            f"IF {cond} THEN\n"
            f"  {then_part}\n"
            f"ELSE\n"
            f"  {else_part}\n"
            f"END"
        )

    return (
        f"IF {cond} THEN\n"
        f"  {then_part}\n"
        f"END"
    )


def gen_while(depth):
    cond = f"{random.choice(var_names)} < {random.randint(100,1000)}"

    body = ";\n".join(
        gen_stmt(depth + 1)
        for _ in range(random.randint(2, 5))
    )

    return (
        f"WHILE {cond} DO\n"
        f"{body}\n"
        f"END"
    )


def gen_stmt(depth=0):
    if depth >= MAX_DEPTH:
        return gen_assign()

    r = random.random()

    if r < 0.6:
        return gen_assign()

    if r < 0.8:
        return gen_if(depth)

    return gen_while(depth)


def gen_proc(i):
    body = ";\n".join(
        gen_stmt()
        for _ in range(STMT_PER_BLOCK)
    )

    return f"""
        PROCEDURE Proc{i};
        BEGIN
        {body}
        END Proc{i}
        """

def generate():
    code = "MODULE StressTest;\n\n"

    code += "VAR\n"
    for v in var_names:
        code += f"  {v}: INTEGER;\n"

    code += "\n"

    for i in range(PROC_COUNT):
        code += gen_proc(i)
        code += ";\n\n"

    code += "BEGIN\n"

    for _ in range(STMT_PER_BLOCK):
        code += gen_stmt() + ";\n"

    code += "END StressTest.\n"

    return code


with open("testcases/stress.ob0", "w") as f:
    f.write(generate())

print("Generated stress.ob0")