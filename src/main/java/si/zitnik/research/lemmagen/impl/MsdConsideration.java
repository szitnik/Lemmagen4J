package si.zitnik.research.lemmagen.impl;

/// <summary>
/// How algorithm considers msd tags.
/// </summary>
public enum MsdConsideration {
    /// <summary>
    /// Completely ignores mds tags (join examples with different tags and sum their weihgts).
    /// </summary>
    Ignore,
    /// <summary>
    /// Same examples with different msd's are not considered equal and joined.
    /// </summary>
    Distinct,
    /// <summary>
    /// Joins examples with different tags (concatenates all msd tags).
    /// </summary>
    JoinAll,
    /// <summary>
    /// Joins examples with different tags (concatenates just distinct msd tags - somehow slower).
    /// </summary>
    JoinDistinct,
    /// <summary>
    /// Joins examples with different tags (new tag is the left to right substring that all joined examples share).
    /// </summary>
    JoinSameSubstring
}        