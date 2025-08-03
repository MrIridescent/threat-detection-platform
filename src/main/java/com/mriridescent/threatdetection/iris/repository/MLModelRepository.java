package com.mriridescent.threatdetection.iris.repository;
package com.mriridescent.threatdetection.iris.repository;

import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ML model entity operations.
 */
@Repository
public interface MLModelRepository extends JpaRepository<MLModel, Long> {

    /**
     * Find a model by name
     *
     * @param name Model name
     * @return Optional containing the model if found
     */
    Optional<MLModel> findByName(String name);

    /**
     * Find all active models
     *
     * @return List of active models
     */
    List<MLModel> findByActiveTrue();

    /**
     * Find models by type
     *
     * @param modelType Model type
     * @return List of models of the specified type
     */
    List<MLModel> findByModelType(MLModel.ModelType modelType);

    /**
     * Find active models by type
     *
     * @param modelType Model type
     * @return List of active models of the specified type
     */
    List<MLModel> findByModelTypeAndActiveTrue(MLModel.ModelType modelType);
}
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MLModel entities.
 */
@Repository
public interface MLModelRepository extends JpaRepository<MLModel, Long> {

    /**
     * Finds a model by its active status.
     *
     * @param active Active status to search for
     * @return Optional containing the model if found
     */
    Optional<MLModel> findByActive(boolean active);

    /**
     * Finds models by name.
     *
     * @param name Name to search for
     * @return Optional containing the model if found
     */
    Optional<MLModel> findByName(String name);

    /**
     * Finds models by type.
     *
     * @param modelType Model type to search for
     * @return List of matching models
     */
    List<MLModel> findByModelType(MLModel.ModelType modelType);

    /**
     * Finds models with accuracy above a threshold.
     *
     * @param minAccuracy Minimum accuracy to search for
     * @return List of matching models
     */
    List<MLModel> findByAccuracyGreaterThanEqual(double minAccuracy);
}
