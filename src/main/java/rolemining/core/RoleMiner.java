package rolemining.core;

/** A role mining algorithm. Implementations must not keep state between calls. */
public interface RoleMiner {
    /** Short lower-case name used on the command line, for example "fastminer". */
    String name();

    /** One line: what it does, its parameters and their defaults. */
    String description();

    MiningResult mine(UpaMatrix upa, MinerOptions options);
}
